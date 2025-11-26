import {Component, OnInit} from '@angular/core';
import {ShippingService} from "../../../../core/services/shipping.service";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {OrderStatus, OrderStatusMeta} from "../../../../shared/status/order-status";
import {ethers} from "ethers";

export interface OrderHistoryRecord {
  status: OrderStatus | string;   // enum hoặc fallback string
  location: string;
  timestamp: string;
  actor?: string;                 // tùy backend có gửi
}

/** Đơn hàng đang giao từ blockchain */
export interface ShippingOrder {
  shipmentID: string;
  orderId?: string;
  orderCode?: string;
  cargo: string;                  // ví dụ: "3x iPhone, 1x MacBook"
  userId: string;                  // MSP hoặc userId
  currentLocation: string;
  status: OrderStatus;
  lastUpdated: string;            // ISO string
  shippingAddress?: string;
  totalAmount?: number;
  updatesHistory?: OrderHistoryRecord[];
}

@Component({
  selector: 'app-shipping',
  standalone: true,
  imports: [
    NgIf,
    NgForOf,
    DatePipe
  ],
  templateUrl: './shipping.component.html',
  styleUrl: './shipping.component.scss'
})
export class ShippingComponent implements OnInit {
  shippingOrders: ShippingOrder[] = [];
  selectedOrder: ShippingOrder | null = null;
  orderHistory: OrderHistoryRecord[] = [];
  loading = false;
  loadingHistory = false;
  loadingPayment: string | null = null;

  protected readonly OrderStatusMeta = OrderStatusMeta;

  constructor(private shippingService: ShippingService) {
  }

  ngOnInit(): void {
    this.loadShippingOrders();
  }

  /** 📦 Lấy danh sách đơn hàng đang giao */
  loadShippingOrders(): void {
    this.loading = true;
    this.shippingService.getShippingOrders().subscribe({
      next: (res: any) => {
        this.shippingOrders = (res.data || []).map((ship: any) => ({
          ...ship,
          status: this.mapShipmentStatusToOrderStatus(ship.status)
        }));
        this.loading = false;
      },
      error: (err) => {
        console.error('❌ Lỗi khi tải danh sách vận chuyển:', err);
        this.loading = false;
      }
    });
  }

  /** Xem lịch sử blockchain */
  viewHistory(order: any): void {
    console.log('🟢 Xem lịch sử đơn hàng:', order);

    this.selectedOrder = order;
    this.loadingHistory = true;

    this.shippingService.getOrderHistory(order.shipmentID || order.orderId).subscribe({
      next: (res: any) => {
        console.log('📜 Kết quả lịch sử:', res);
        this.orderHistory = res.data || res;
        this.loadingHistory = false;
      },
      error: (err) => {
        console.error('❌ Lỗi khi lấy lịch sử:', err);
        this.loadingHistory = false;
      }
    });
  }


  /** ✅ Xác nhận đã nhận hàng */
  confirmReceived(order: any): void {
    if (!confirm(`Xác nhận bạn đã nhận đơn hàng ${order.shipmentID || order.orderCode}?`)) return;

    this.shippingService.confirmReceived(order.shipmentID || order.orderId, order.userId).subscribe({
      next: () => {
        alert('📦 Bạn đã xác nhận đã nhận hàng!');
        this.loadShippingOrders();
      },
      error: (err) => {
        alert('❌ Không thể xác nhận: ' + (err.error?.error || err.message));
      }
    });
  }

  /** Đóng modal */
  closeModal(): void {
    this.selectedOrder = null;
    this.orderHistory = [];
  }

  getStatusMeta(rawStatus: string) {
    const status = OrderStatus[rawStatus as keyof typeof OrderStatus];

    if (status && OrderStatusMeta[status]) return OrderStatusMeta[status];

    return {
      label: rawStatus,
      color: '#374151',
      bgColor: '#E5E7EB'
    };
  }


  canPay(order: ShippingOrder): boolean {
    return ['PENDING'].includes(order.status);
  }


  canConfirm(order: ShippingOrder): boolean {
    return ['DELIVERED'].includes(order.status);
  }


  async payWithMetaMask(shipmentId: string, amountEth: string) {
    this.loadingPayment = shipmentId;

    try {
      const ethereum = (window as any).ethereum;
      if (!ethereum) {
        alert("⚠ Vui lòng cài MetaMask!");
        return;
      }

      await ethereum.request({method: "eth_requestAccounts"});

      const provider = new ethers.BrowserProvider(ethereum);
      const signer = await provider.getSigner();

      // Lấy network ban đầu
      let network = await provider.getNetwork();
      console.log("CHAIN ID REAL:", network.chainId);

      // Nếu đang ở Mainnet => ép chuyển sang Sepolia
      if (network.chainId === 1n) {
        try {
          await ethereum.request({
            method: "wallet_switchEthereumChain",
            params: [{chainId: "0xaa36a7"}],
          });
        } catch (err: any) {
          if (err.code === 4902) {
            await ethereum.request({
              method: "wallet_addEthereumChain",
              params: [{
                chainId: "0xaa36a7",
                chainName: "Sepolia Test Network",
                rpcUrls: ["https://rpc.sepolia.org"],
                nativeCurrency: {
                  name: "SepoliaETH",
                  symbol: "ETH",
                  decimals: 18,
                },
                blockExplorerUrls: ["https://sepolia.etherscan.io"],
              }],
            });
          }
        }

        // ❗ LẤY LẠI CHAIN CHÍNH XÁC
        network = await provider.getNetwork();
      }

      if (Number(network.chainId) !== 11155111) {
        alert("⚠ Vui lòng chuyển MetaMask sang Sepolia!");
        return;
      }

      // Contract
      const contract = new ethers.Contract(
        "0xB55AFfC98D2AA4d1E4ccfF9456B2f229dB15998B",
        ["function payOrder(string orderId) payable"],
        signer
      );

      alert("⏳ Đang gửi giao dịch...");

      const tx = await contract["payOrder"](shipmentId, {
        value: ethers.parseEther(amountEth)
      });


      console.log("TX sent:", tx.hash);

      const receipt = await tx.wait();
      console.log("TX mined:", receipt);

      // ❗ ETHERS v6 dùng 1 thay vì 1n
      if (receipt.status === 1) {
        alert("🎉 Thanh toán crypto thành công!");

        this.loadShippingOrders();
      } else {
        alert("❌ Giao dịch thất bại!");
      }

    } catch (error: any) {
      console.error("❌ Lỗi thanh toán:", error);

      if (error.code === "ACTION_REJECTED") {
        alert("❌ Bạn đã từ chối giao dịch.");
      } else {
        alert("❌ Lỗi: " + error.message);
      }
    }
  }


  mapShipmentStatusToOrderStatus(raw: string) {
    switch (raw) {
      case 'CREATED':
        return 'PENDING'; // hoặc 'CONFIRMED' tùy logic backend

      case 'PAYMENT_PENDING':
        return 'PENDING';

      case 'PAID':
        return 'CONFIRMED';

      case 'PACKING':
        return 'PROCESSING';

      case 'SHIPPING':
        return 'SHIPPING';

      case 'DELIVERED':
        return 'DELIVERED';

      case 'RECEIVED':
        return 'COMPLETED';

      default:
        return raw;
    }
  }

}

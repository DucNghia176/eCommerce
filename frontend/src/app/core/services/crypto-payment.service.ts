import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ethers} from "ethers";

@Injectable({
  providedIn: 'root'
})
export class CryptoPaymentService {

  contractAddress = "YOUR_CONTRACT_ADDRESS";
  abi = [
    "function payOrder(string orderId) external payable",
    "event OrderPaid(string orderId, address payer, uint amount, uint timestamp)"
  ];

  constructor(private http: HttpClient) {
  }

  async connectWallet(): Promise<string> {
    const provider = new ethers.BrowserProvider((window as any).ethereum);
    const accounts = await provider.send("eth_requestAccounts", []);
    return accounts[0];
  }

  async pay(orderId: string, amountEth: number): Promise<string> {
    const provider = new ethers.BrowserProvider((window as any).ethereum);
    const signer = await provider.getSigner();
    const contract = new ethers.Contract(this.contractAddress, this.abi, signer);

    const tx = await contract.payOrder(orderId, {
      value: ethers.parseEther(amountEth.toString())
    });

    return tx.hash;
  }

  verifyPayment(orderId: string, txHash: string) {
    return this.http.post("http://localhost:8085/api/payment/verify", {
      orderId, txHash
    });
  }
}

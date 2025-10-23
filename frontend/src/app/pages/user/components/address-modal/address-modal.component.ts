import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {NzOptionComponent, NzSelectComponent} from "ng-zorro-antd/select";
import {FormsModule} from "@angular/forms";
import {NgForOf} from "@angular/common";
import {NzInputDirective} from "ng-zorro-antd/input";
import {NzButtonComponent} from "ng-zorro-antd/button";
import {NzMessageService} from "ng-zorro-antd/message";

@Component({
  selector: 'app-address-modal',
  standalone: true,
  imports: [
    NzSelectComponent,
    FormsModule,
    NzOptionComponent,
    NgForOf,
    NzInputDirective,
    NzButtonComponent
  ],
  templateUrl: './address-modal.component.html',
  styleUrls: ['./address-modal.component.scss']
})
export class AddressModalComponent implements OnInit {
  @Output() onSave = new EventEmitter<any>();

  provinces: any[] = [];
  wards: any[] = [];

  selectedProvince: any;
  selectedWard: any;
  detail = '';

  baseUrl = '/api-province/api/v2';

  constructor(private http: HttpClient, private message: NzMessageService,) {
  }

  ngOnInit(): void {
    // Lấy tất cả tỉnh/thành
    this.http.get(`${this.baseUrl}/p/`)
      .subscribe((res: any) => {
        this.provinces = res;
      });
  }

  onProvinceChange(province: any): void {
    this.selectedProvince = province;
    if (province?.code) {
      const provinceCode = String(province.code);
      this.http.get(`${this.baseUrl}/p/${provinceCode}?depth=2`).subscribe({
        next: (res: any) => {
          // Nếu có districts, dùng districts.flatMap(wards)
          if (res?.districts && Array.isArray(res.districts)) {
            this.wards = res.districts.flatMap((d: any) => d.wards || []);
          }
          // Nếu API trả wards trực tiếp
          else if (res?.wards && Array.isArray(res.wards)) {
            this.wards = res.wards;
          } else {
            this.wards = [];
          }
          this.selectedWard = null;
        },
        error: err => {
          console.error('Lỗi khi lấy phường/xã:', err);
          this.wards = [];
          this.selectedWard = null;
        }
      });
    } else {
      this.wards = [];
      this.selectedWard = null;
    }
  }

  save(): void {
    if (!this.selectedProvince || !this.selectedWard || !this.detail.trim()) {
      // Có thể dùng toast hoặc alert
      this.message.warning('Vui lòng nhập đầy đủ tỉnh/thành, phường/xã và địa chỉ chi tiết!');
      return;
    }
    const parts = [this.detail, this.selectedWard?.name, this.selectedProvince?.name].filter(Boolean);
    const fullAddress = parts.join(', ');

    // Lấy danh sách địa chỉ đã lưu
    const saved = JSON.parse(localStorage.getItem('savedAddresses') || '[]');
    saved.push(fullAddress);

    // Lưu lại vào localStorage
    localStorage.setItem('savedAddresses', JSON.stringify(saved));

    // Emit để CartTotalComponent nhận
    this.onSave.emit({fullAddress});

    // Reset form
    this.detail = '';
    this.selectedProvince = null;
    this.selectedWard = null;
  }
}

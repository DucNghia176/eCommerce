import {Component} from '@angular/core';
import {NzModalModule, NzModalService} from "ng-zorro-antd/modal";
import {Router} from "@angular/router";

@Component({
  selector: 'app-auth-modal',
  standalone: true,
  imports: [NzModalModule],
  templateUrl: './auth-modal.component.html',
  styleUrl: './auth-modal.component.scss'
})
export class AuthModalComponent {
  constructor(private modal: NzModalService, private router: Router) {
  }

  openModal() {
    this.modal.confirm({
      nzTitle: `
      <div class="flex flex-col items-center justify-center text-sky-600">
          <div class="bg-sky-100 p-3 rounded-full mb-2">
            <i class="fa-solid fa-lock text-2xl"></i>
          </div>
          <span class="font-semibold text-lg">Yêu cầu đăng nhập</span>
        </div>
      `,
      nzContent: `
       <div class="text-center mt-3 text-gray-600">
          <p class="mb-3">Bạn cần đăng nhập để thực hiện thao tác này.</p>
          <p class="text-sm text-gray-400 italic">Chỉ mất vài giây để đăng nhập hoặc tạo tài khoản mới.</p>
        </div>
      `,
      nzOkText: 'Đăng nhập',
      nzCancelText: 'Đăng ký',
      nzOkType: 'primary',
      nzCentered: true,
      nzWidth: 380,
      nzClassName: 'tailwind-auth-modal',
      nzOnOk: () => this.router.navigate(['/auth/login']),
      nzOnCancel: () => this.router.navigate(['/auth/register'])
    })
  }
}

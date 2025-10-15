import {Injectable} from '@angular/core';
import {NzModalService} from 'ng-zorro-antd/modal';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthModalService {

  constructor(private modal: NzModalService, private router: Router) {
  }

  openModal() {
    const modalRef = this.modal.create({
      nzTitle: `
        <div class="flex flex-col items-center justify-center text-sky-600">
          <div class="bg-sky-100 p-4 rounded-full shadow-sm mb-3">
            <i class="fa-solid fa-lock text-3xl"></i>
          </div>
          <h2 class="text-xl font-semibold">Đăng nhập để tiếp tục</h2>
        </div>
      `,
      nzContent: `
        <div class="text-center mt-5 px-4">
          <p class="text-gray-600 mb-3">
            Bạn cần đăng nhập để thực hiện thao tác này.
          </p>
        </div>
      `,
      nzCentered: true,
      nzWidth: '40%',
      nzMaskClosable: true,
      nzClosable: true,
      nzClassName: 'auth-confirm-modal',
      nzFooter: [
        {
          label: 'Đăng nhập',
          type: 'primary',
          autoLoading: false,
          onClick: () => {
            modalRef.destroy();
            this.router.navigate(['/auth/login']);
          }
        },
        {
          label: 'Đăng ký',
          type: 'default',
          danger: false,
          onClick: () => {
            modalRef.destroy();
            this.router.navigate(['/auth/register']);
          }
        }
      ]
    });
  }
}

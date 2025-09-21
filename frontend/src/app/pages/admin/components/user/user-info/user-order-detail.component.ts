import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {UsersService} from "../../../../../core/services/users.service";
import {UserOrderDetail} from "../../../../../core/models/user.model";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faArrowLeft, faClose, faSave} from "@fortawesome/free-solid-svg-icons";
import {CurrencyPipe, Location, NgForOf, NgIf, NgOptimizedImage} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {StatusDirective} from "../../../../../shared/directive/status.directive";
import {OrderStatusMeta} from "../../../../../shared/status/order-status";
import {ToastService} from "../../../../../core/services/toast.service";

@Component({
  selector: 'app-user-order-detail',
  standalone: true,
  imports: [
    FaIconComponent,
    FormsModule,
    NgIf,
    NgForOf,
    StatusDirective,
    CurrencyPipe,
    NgOptimizedImage
  ],
  templateUrl: './user-order-detail.component.html',
  styleUrl: './user-order-detail.component.scss'
})
export class UserOrderDetailComponent implements OnInit {
  userId!: number;
  countOrder: number = 0;
  userDetail!: UserOrderDetail;
  isLoading = false;
  message: string | null = null;
  protected readonly faSave = faSave;
  protected readonly faClose = faClose;
  protected readonly faArrowLeft = faArrowLeft;
  protected readonly OrderStatusMeta = OrderStatusMeta;
  private router = inject(ActivatedRoute)
  private userService = inject(UsersService)
  private location = inject(Location)
  private toastService = inject(ToastService)

  ngOnInit() {
    this.router.paramMap.subscribe(params => {
      const id = params.get("id");
      if (id) {
        this.userId = +id;
        this.loadUserOrderDetail();
      }
    })
  }

  goBack() {
    this.location.back();
  }

  loadUserOrderDetail() {
    this.userService.userOrderDetail(this.userId).subscribe({
      next: (user) => {
        this.userDetail = user;
        this.countOrder = user.userOrderDetailResponse?.length || 0
      }
    })
  }

  deleteUser() {
    this.userService.deleteUser(this.userId).subscribe({
      next: (user) => {
        this.toastService.show(`Xóa người dùng userId = ${this.userId} thành công`, "p")
        this.location.back()
      },
      error: (err) => {
        this.toastService.show("Xóa người dùng thất bại" + err.message, "f")
      }
    })
  }
}

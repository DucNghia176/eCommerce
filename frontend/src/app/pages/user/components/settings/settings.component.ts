import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgIf} from "@angular/common";

interface UserProfile {
  fullName: string;
  email: string;
  phone: string;
  address: string;
  city: string;
  district: string;
}

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf
  ],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss'
})
export class SettingsComponent implements OnInit {
  profileForm!: FormGroup;
  passwordForm!: FormGroup;
  activeTab: string = 'profile';
  isLoading: boolean = false;
  successMessage: string = '';

  constructor(private fb: FormBuilder) {
  }

  ngOnInit() {
    this.initForms();
    this.loadUserProfile();
  }

  initForms() {
    this.profileForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      address: ['', Validators.required],
      city: ['', Validators.required],
      district: ['', Validators.required]
    });

    this.passwordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    });
  }

  loadUserProfile() {
    // Dữ liệu mẫu - thay bằng API call
    const userData: UserProfile = {
      fullName: 'Nguyễn Văn A',
      email: 'nguyenvana@email.com',
      phone: '0123456789',
      address: '123 Nguyễn Huệ',
      city: 'TP.HCM',
      district: 'Quận 1'
    };
    this.profileForm.patchValue(userData);
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
    this.successMessage = '';
  }

  onSubmitProfile() {
    if (this.profileForm.valid) {
      this.isLoading = true;
      // Giả lập API call
      setTimeout(() => {
        this.isLoading = false;
        this.successMessage = 'Cập nhật thông tin thành công!';
        console.log('Profile updated:', this.profileForm.value);
        setTimeout(() => this.successMessage = '', 3000);
      }, 1000);
    } else {
      Object.keys(this.profileForm.controls).forEach(key => {
        this.profileForm.get(key)?.markAsTouched();
      });
    }
  }

  onSubmitPassword() {
    if (this.passwordForm.valid) {
      const {newPassword, confirmPassword} = this.passwordForm.value;
      if (newPassword !== confirmPassword) {
        alert('Mật khẩu xác nhận không khớp!');
        return;
      }

      this.isLoading = true;
      // Giả lập API call
      setTimeout(() => {
        this.isLoading = false;
        this.successMessage = 'Đổi mật khẩu thành công!';
        this.passwordForm.reset();
        setTimeout(() => this.successMessage = '', 3000);
      }, 1000);
    } else {
      Object.keys(this.passwordForm.controls).forEach(key => {
        this.passwordForm.get(key)?.markAsTouched();
      });
    }
  }
}

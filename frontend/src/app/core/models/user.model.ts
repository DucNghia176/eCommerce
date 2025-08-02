export interface UserResponse {
  id: number;
  username: string;
  email: string;
  fullName: string;
  dateOfBirth: Date;
  gender: string;
  isLock: number;
  role: string;
  avatar: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface UserRequest {
  username: string;
  password: string;
  fullName?: string;
  email: string;
  gender?: string;
  dateOfBirth?: Date;
}

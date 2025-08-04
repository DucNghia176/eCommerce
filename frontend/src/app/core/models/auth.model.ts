export interface AuthRequest {
  usernameOrEmail: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  fullName?: string;
  email: string;
  gender?: string;
  dateOfBirth?: Date;
}

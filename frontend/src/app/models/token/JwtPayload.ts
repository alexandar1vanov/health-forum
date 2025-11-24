export interface JwtPayload {
  sub?: string | number;
  userId?: number;
  email?: string;
  exp?: number;
  role?: string
}

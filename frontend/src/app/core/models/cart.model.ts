export interface CartItem {
  cart_item_id: number;
  productId: number;
  quantity: number;
  unitPrice: number;
  name: string;
  imageUrl?: string;
}

export interface Cart {
  id: number;
  userId: number;
  items: CartItem[];
  totalAmount: number;
}

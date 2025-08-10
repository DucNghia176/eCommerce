export interface ProductResponse {
  id: number,
  name: string,
  description: string,
  price: string,
  discountPrice: string,
  categoryName: string,
  thumbnailUrl: string,
  imageUrls: string[],
  tags: string[]
  unit: string,
  quantity: number,
  brandId: string,
}

export interface ProductRequest {
  name: string,
  description: string,
  price: string,
  discount: string,
  categoryId: number | null,
  tags: string[],
  unit: string,
  brandId: number | null,
}

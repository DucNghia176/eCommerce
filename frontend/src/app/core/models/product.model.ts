import {BrandResponse, TagResponse} from "./TagBrand.model";

export interface ProductResponse {
  id: number,
  name: string,
  description: string,
  price: string,
  discountPrice: string,
  categoryId: number,
  categoryName: string,
  thumbnailUrl: string,
  imageUrls: string[],
  tags: TagResponse[],
  unit: string,
  quantity: number,
  skuCode: string,
  brand: BrandResponse,
}

export interface ProductRequest {
  name: string,
  description: string,
  price: string,
  discount: string,
  categoryId: number | null,
  tags: number[] | null,
  unit: string,
  brandId: number | null,
}

export interface ProductSearchRequest {
  name?: string;
  priceFrom?: number;
  priceTo?: number;
  price?: number;
  discountPrice?: number;
  categoryName?: string;
  tagName?: number[];
  hasDiscount?: boolean;
}

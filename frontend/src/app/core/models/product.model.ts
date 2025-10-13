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
  attributes: ProductAttributeResponse[]
}

export interface CreateProductResponse {
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
  attributes: ProductAttributeResponse[]
}

export interface SearchProductResponse {
  id: number,
  name: string,
  thumbnailUrl: string,
  price: string,
  discountPrice: string,
  score: string,
  user: number,
}

export interface ProductViewResponse extends ProductResponse {
  score: string,
  user: number,
  relatedProducts: number[],
}

export interface ProductByTagResponse extends ProductResponse {
  score: string,
  user: number,
}

export interface ProductAttributeResponse {
  attribute: string,
  value: string[]
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
  attributes: ProductAttributeResponse[]
}

export interface CreateProductRequest {
  name: string,
  description: string,
  price: string,
  discount: string,
  categoryId: number | null,
  tags: number[] | null,
  unit: string,
  brandId: number | null,
  attributes: AttributeRequest[]
}

export interface AttributeRequest {
  attributeName: string,
  attributeValueName: string,
}

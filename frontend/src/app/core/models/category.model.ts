import {BrandResponse} from "./TagBrand.model";

export interface CategoryResponse {
  id: number,
  name: string,
  parentId: number,
  image: string,
  productCount: number
  isVisible: number,
}

export interface CategoryRequest {
  name: string,
  parentId?: number,
  isVisible?: number,
}

export interface ChildCategoryResponse {
  id: number,
  name: string,
  brands: BrandResponse[]
  products: FeaturedProductResponse[]
}

export interface FeaturedProductResponse {
  id: number,
  name: string,
  price: number,
  imageUrl: string,
}

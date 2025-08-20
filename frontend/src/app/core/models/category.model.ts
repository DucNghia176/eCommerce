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

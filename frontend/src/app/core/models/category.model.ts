export interface CategoryResponse {
  id: number,
  name: string,
  parentId: number,
  image: string,
}

export interface CategoryRequest {
  name: string,
  parentId?: number,
  image?: File,
}

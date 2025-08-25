export interface InventoryResponse {
  skuCode: string,
  quantity: number,
  reservedQuantity: number,
  importPrice: string,
}

export interface InventoryRequest {
  skuCode?: string,
  quantity?: number,
}

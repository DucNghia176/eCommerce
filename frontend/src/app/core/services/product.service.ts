import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {
  CreateProductRequest,
  CreateProductResponse,
  ProductByTagResponse,
  ProductRequest,
  ProductResponse,
  ProductViewResponse,
  SearchProductResponse,
} from "../models/product.model";
import {ApiResponse} from "../models/common.model";
import {Page} from "../models/page.model";

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private apiUrl = 'http://localhost:8085/api/product';

  constructor(private http: HttpClient) {
  }

  createProduct(request: CreateProductRequest, images ?: File[]): Observable<CreateProductResponse> {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(request)], {type: 'application/json'}));
    if (images) {
      images.forEach(image => formData.append('images', image));
    }
    return this.http.post<ApiResponse<CreateProductResponse>>(`${this.apiUrl}/create`, formData)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message);
        })
      );
  }

  getAllProduct(page: number = 0, size: number = 10): Observable<Page<ProductResponse>> {
    const params: any = {
      page: page,
      size: size,
    };

    return this.http.get<ApiResponse<Page<ProductResponse>>>(`${this.apiUrl}`, {
      params
    }).pipe(
      map(response => {
        if (response.code === 200 && response.data) {
          return response.data;
        }
        throw new Error(response.message || 'Something went wrong');
      }));
  }

  getProductById(id: number): Observable<ProductResponse> {
    return this.http.get<ApiResponse<ProductResponse>>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message || 'Something went wrong');
        }));
  }

  updateProduct(id: number, request: ProductRequest, images ?: File[]): Observable<ProductResponse> {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(request)], {type: 'application/json'}));
    if (images) {
      images.forEach(image => formData.append('images', image));
    }
    return this.http.put<ApiResponse<ProductResponse>>(`${this.apiUrl}/update/${id}`, formData)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message);
        })
      );
  }

  deleteProduct(id: number): Observable<ProductResponse> {
    return this.http.delete<ApiResponse<ProductResponse>>(`${this.apiUrl}/delete/${id}`)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message);
        }));
  }

  searchProduct(params: {
    keyword?: string,
    categoryId?: number,
    brandId?: number[],
    priceFrom?: number,
    priceTo?: number,
    ratingFrom?: number,
    page?: number;
    size?: number;
    sort?: string;
  }): Observable<Page<SearchProductResponse>> {
    let queryParams = new HttpParams();
    Object.keys(params).forEach((key: string) => {
      const value = (params as any)[key];
      if (value !== undefined && value !== null) {
        if (Array.isArray(value)) {
          value.forEach(v => {
            queryParams = queryParams.append(key, encodeURIComponent(v.toString()));
          });
        } else {
          queryParams = queryParams.append(key, encodeURIComponent(value.toString()));
        }
      }
    });
    return this.http.get<ApiResponse<Page<SearchProductResponse>>>(`${this.apiUrl}/search`, {params: queryParams})
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message);
        })
      );
  }

  viewProduct(id?: number): Observable<ProductViewResponse> {
    return this.http.get<ApiResponse<ProductViewResponse>>(`${this.apiUrl}/view/${id}`)
      .pipe(map(
        response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message);
        }
      ))
  }

  productByTag(params: {
    tags?: string[];
    page?: number;
    size?: number;
    sort?: string;
  }): Observable<Page<ProductByTagResponse>> {
    let queryParam = new HttpParams();

    if (params.tags && params.tags.length > 0) {
      params.tags.forEach(tag => {
        queryParam = queryParam.append('tags', tag);
      });
    }
    if (params.page != null) queryParam = queryParam.set('page', params.page);
    if (params.size != null) queryParam = queryParam.set('size', params.size);
    if (params.sort) queryParam = queryParam.set('sort', params.sort);

    return this.http.get<ApiResponse<Page<ProductByTagResponse>>>(`${this.apiUrl}/productByTag`, {params: queryParam})
      .pipe(map(
        response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message);
        }
      ))
  }
}

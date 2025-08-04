export enum PageSizeEnum {
  SMALL = 10,
  MEDIUM = 50,
  LARGE = 100
}

export const PageSize: number[] = Object
  .values(PageSizeEnum)
  .filter((value): value is number => typeof value === 'number');

import { HttpParams } from '@angular/common/http';

/**
 * Adds a query param to HttpParams object
 * @param param Params object
 * @param key Query parameter name
 * @param value Query parameter value
 * @returns parameter
 */
export function addHttpParam(param: HttpParams, key: string, value: any): HttpParams {
  if (value === undefined) return param;
  return param.set(key, value.toString());
}

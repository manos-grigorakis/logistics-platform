/**
 * Returns a translation key based on the HTTP status code
 *
 * The returned key must be used along with the translation pipe
 *
 * Mappings:
 * - 500 -> common.errors.server
 * - 503 -> common.errors.storage-unavailable
 * - default -> common.errors.generic
 * @param status The HTTP status code
 * @returns The translation key for the associated status code
 */
export function handleHttpErrors(status: number): string {
  if (status === 500) return 'common.errors.server';
  else if (status === 503) return 'common.errors.storage-unavailable';
  return 'common.errors.generic';
}

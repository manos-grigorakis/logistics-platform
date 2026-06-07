/**
 * Returns a translation key based on the HTTP status code
 *
 * The returned key must be used along with the translation pipe
 *
 * Mappings:
 * - 500 -> common.errors.server
 * - default -> common.errors.generic
 * @param status The HTTP status code
 * @returns The translation key for the associated status code
 */
export function handleHttpErrors(status: number): string {
  if (status === 500) return 'common.errors.server';
  return 'common.errors.generic';
}

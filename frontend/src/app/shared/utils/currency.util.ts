/**
 * el-GR money format
 *
 * dot = thousands (optional) \
 * comma or dot = decimal
 */
export const GREEK_AMOUNT_PATTERN = /^\d{1,3}(\.\d{3})*([.,]\d{1,2})?$|^\d+([.,]\d{1,2})?$/;

export function parseGreekAmount(raw: string): number {
  const cleaned = raw.trim();

  // Comma as decimal separator
  // Example: "1.234,56" -> 1234.56
  if (cleaned.includes(',')) {
    return parseFloat(cleaned.replace(/\./g, '').replace(',', '.'));
  }

  // Without comma the last dot is treated as decimal separator only if it has 1-2 digits after it
  // Example: "1234.56" -> 1234.56
  const trailingDigits = cleaned.match(/\.(\d+)$/)?.[1];
  if (trailingDigits && trailingDigits.length <= 2) {
    return parseFloat(cleaned);
  }

  // Otherwise, all dots are treated as thousand separator
  // Example: "1.234.567" -> 1234567
  return parseFloat(cleaned.replace(/\./g, ''));
}

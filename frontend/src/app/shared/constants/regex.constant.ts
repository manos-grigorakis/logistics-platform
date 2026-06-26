export const REGEX = {
  HEX_COLOR: /^#[0-9A-Fa-f]{6}$/,
  URL: /^https?:\/\/.+/,
  ONLY_DIGITS: /^\d{9}$/,
} as const;

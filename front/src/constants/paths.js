const { REACT_APP_BASE_URL } = process.env;

export const BASE_URL = REACT_APP_BASE_URL ?? "http://localhost:8080/v1";

export const CLIENTS_URL = `${BASE_URL}/clients`;
export const WALLETS_URL = `${BASE_URL}/wallets`;
export const WALLET_MONTH_URL = `${BASE_URL}/monthly-wallets`;
export const TRANSACTION_URL = `${BASE_URL}/transactions`;
export const STOCKS_URL = `${BASE_URL}/stocks`;
export const ASSET_URL = `${BASE_URL}/assets`;
export const TAX_URL = `${BASE_URL}/taxes`;
export const AUTH_URL = `${BASE_URL}/authentication`;

export const percentFormatter = Intl.NumberFormat("pt-BR", {
  style: "percent",
  minimumFractionDigits: 2,
  maximumFractionDigits: 2,
});

export const numberFormatter = Intl.NumberFormat("pt-BR");

export const moneyFormatter = Intl.NumberFormat("pt-BR", {
  style: "currency",
  currency: "BRL",
});

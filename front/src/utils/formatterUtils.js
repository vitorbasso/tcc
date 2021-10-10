const THOUSAND = { value: 1_000, symbol: "k" };
const MILLION = { value: 1_000_000, symbol: "m" };
const BILLION = { value: 1_000_000_000, symbol: "b" };

export const percentFormatter = Intl.NumberFormat("pt-BR", {
  style: "percent",
  minimumFractionDigits: 2,
  maximumFractionDigits: 2,
  signDisplay: "always",
});

export const percentFormatterWithoutSign = Intl.NumberFormat(
  "pt-BR",
  Object.assign(percentFormatter.resolvedOptions(), { signDisplay: "never" })
);

export const percentFormatterClean = (number) => {
  return percentFormatterWithoutSign.format(number).split("%")?.[0];
};

export const numberFormatter = Intl.NumberFormat("pt-BR");

export const numberFormatterDecimal = Intl.NumberFormat("pt-BR", {
  minimumFractionDigits: 2,
  maximumFractionDigits: 2,
});

export const moneyFormatter = Intl.NumberFormat("pt-BR", {
  style: "currency",
  currency: "BRL",
});

export const moneyFormatterWithoutSymbol = (number) => {
  return number.toLocaleString("pt-BR", {
    maximumFractionDigits:
      moneyFormatter.resolvedOptions().maximumFractionDigits,
  });
};

export const dateFormatter = Intl.DateTimeFormat("pt-BR", {
  timeStyle: "short",
  dateStyle: "short",
});

function getFormattedNumber(integer, divider) {
  const shorter = (Math.abs(integer / divider.value) + "").split(".");
  shorter[1] = (shorter[1]?.substr(0, 2) ?? "00").padEnd(2, "0");
  return shorter.join(",") + divider.symbol;
}

export function abbreviateNumber(number) {
  if (number < THOUSAND.value)
    return numberFormatterDecimal.format(Math.abs(number));
  if (number < MILLION.value) return getFormattedNumber(number, THOUSAND);
  if (number < BILLION.value) return getFormattedNumber(number, MILLION);
  return getFormattedNumber(number, BILLION);
}

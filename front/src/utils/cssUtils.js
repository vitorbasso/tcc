export function getMoneyClass(money) {
  const moneyLength = ("" + money).split(".")[0].length;
  return moneyLength < 7 ? "money" : moneyLength < 10 ? "money-7" : "money-10";
}

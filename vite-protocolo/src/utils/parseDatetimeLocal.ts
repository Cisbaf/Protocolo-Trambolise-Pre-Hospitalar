export function parseDatetimeLocal(
  value?: string
): Date | undefined {
  if (!value) return undefined;

  const [date, time] = value.split("T");
  if (!date || !time) return undefined;

  const [year, month, day] = date.split("-").map(Number);
  const [hour, minute] = time.split(":").map(Number);

  return new Date(year, month - 1, day, hour, minute);
}
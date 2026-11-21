import { getCountries, getCountryCallingCode, parsePhoneNumberFromString, type CountryCode } from 'libphonenumber-js/min'

const tenNuoc = new Intl.DisplayNames('vi', { type: 'region' })
const sapXep = new Intl.Collator('vi')
export const cacQuocGia = getCountries().map(ma => ({
  ma,
  ten: tenNuoc.of(ma) ?? ma,
  maGoi: '+' + getCountryCallingCode(ma),
})).sort((a, b) => sapXep.compare(a.ten, b.ten))

export function chuanHoaSoDienThoai(giaTri: string, quocGia: CountryCode) {
  const so = parsePhoneNumberFromString(giaTri, { defaultCountry: quocGia, extract: false })
  return so?.isPossible() && !so.ext ? so.number : ''
}

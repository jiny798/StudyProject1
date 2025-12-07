import { DateTimeFormatter, LocalDateTime } from '@js-joda/core'
export default class CouponWrite {
  public name: string = ''
  public description: string = ''

  public discountType: string = 'PERCENTAGE'
  public discountValue: number = 0

  public expirationType: string = 'DATE_RANGE'
  public validDays: number | null = null

  public startDate: LocalDateTime | null = null
  public endDate: LocalDateTime | null = null

  public minOrderAmount: number = 0
  public maxDiscountAmount: number = 0

  public totalQuantity: number = 0
}

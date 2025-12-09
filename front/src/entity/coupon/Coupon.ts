export default class CouponResponse {
// 식별자 (생성 시에는 없지만, 조회 시에는 필수)
  id: number = 0
  name: string = ''
  description: string = ''

  // Java Enum: DiscountType (PERCENTAGE, FIXED)
  discountType: string = 'PERCENTAGE'

  discountValue: number = 0
  maxDiscountAmount: number = 0
  minOrderAmount: number = 0

  // Java Enum: ExpirationType (DATE_RANGE, VALID_DAYS_ON_ISSUE)
  expirationType: string = 'DATE_RANGE'

  startDate: string | null = null
  endDate: string | null = null
  validDays: number | null = null
  totalQuantity: number = 0
  issuedQuantity: number = 0

  // Java Enum: CouponStatus (ACTIVE, INACTIVE)
  status: string = 'ACTIVE'
  createdAt: string = ''

}

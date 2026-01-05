import {LocalDateTime} from "@js-joda/core";

export default class MyCoupon {
  userCouponId: number = 0
  name: string = ''
  description: string = ''
  // Java Enum: DiscountType (PERCENTAGE, FIXED)
  discountType: string = 'PERCENTAGE'
  discountValue: number = 0

  isUsed: boolean = false
  issuedAt: LocalDateTime | null = null
  expirationDate: LocalDateTime | null = null

}

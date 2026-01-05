import {inject, singleton} from 'tsyringe'
import HttpRepository from '@/repository/user/HttpRepository.ts'
import CouponResponse from "@/entity/coupon/Coupon.ts";
import MyCoupon from "@/entity/user/coupon/MyCoupon.ts";

@singleton()
export default class CouponRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {
  }

  public getMyCouponList() {
    return this.httpRepository.getAll<MyCoupon>(
      {
        path: `/api/coupons/my`,
      },
      MyCoupon,
    )
  }

}

import type OrderProductRequest from "@/entity/order/user/OrderProductRequest.ts";

export default class OrderRequest {
  public products: OrderProductRequest[] = []
  public shippingAddress = ''
  public userCouponId = 0
}

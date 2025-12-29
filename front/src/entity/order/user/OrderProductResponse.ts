import type OrderProductResponseDto from "@/entity/order/user/OrderProductResponseDto.ts";

export default class OrderProductResponse {
  public orderId = 0
  public products: OrderProductResponseDto[] = []
  public totalPrice = 0
  public discountPrice = 0
}

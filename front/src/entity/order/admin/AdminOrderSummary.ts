import {LocalDateTime} from "@js-joda/core";
import AdminOrderProduct from "@/entity/order/admin/AdminOrderProduct.ts";
import {Type} from "class-transformer";

export default class AdminOrderSummary {
  public orderId = 0
  public userEmail = ''
  public userName = ''
  public totalPrice = 0
  public status = ''
  public createdAt: LocalDateTime | null = null

  @Type(() => AdminOrderProduct)
  public orderProducts: AdminOrderProduct[] = []
}

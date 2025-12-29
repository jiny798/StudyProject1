import{ inject, singleton } from 'tsyringe'
import HttpRepository from '@/repository/user/HttpRepository.ts'
import ProductInCart from '@/entity/order/user/ProductInCart.ts'
import type OrderRequest from '@/entity/order/user/OrderRequest.ts'
import ResponseOrderProduct from '@/entity/order/user/OrderProductRequest.ts'
import OrderProduct from '@/entity/order/user/OrderProductRequest.ts'
import AdminOrderSummary from "@/entity/order/admin/AdminOrderSummary.ts";

@singleton()
export default class AdminOrderRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}

  public getOrders(page: number, status: string) {
    return this.httpRepository.getList<AdminOrderSummary>(
      {
        path: `/api/admin/orders?page=${page}&size=10&status=${status}`,
      },
      AdminOrderSummary,
    )
  }

  public startDelivery(orderId: number) {
    return this.httpRepository.post(
      {
        path: `/api/admin/orders/${orderId}/start-delivery`,
      }
    )
  }

  public completeDelivery(orderId: number) {
    return this.httpRepository.post(
      {
        path: `/api/admin/orders/${orderId}/complete-delivery`,
      }
    )
  }

  public cancel() {
    return this.httpRepository.getAll<ResponseOrderProduct>(
      {
        path: `/api/`,
      },
      ResponseOrderProduct,
    )
  }
}

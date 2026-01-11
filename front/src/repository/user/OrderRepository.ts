import { inject, singleton } from 'tsyringe'
import HttpRepository from '@/repository/user/HttpRepository.ts'
import ProductInCart from '@/entity/order/user/ProductInCart.ts'
import type OrderRequest from '@/entity/order/user/OrderRequest.ts'
import OrderProductRequest from '@/entity/order/user/OrderProductRequest.ts'
import OrderProductResponse from "@/entity/order/user/OrderProductResponse.ts"
import UserOrderCount from "@/entity/order/user/UserOrderCount.ts";

@singleton()
export default class OrderRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}

  public order(request: OrderRequest) {
    return this.httpRepository.post({
      path: '/api/orders',
      body: request,
    })
  }

  public getOrders(page: number, startDate: Date, endDate: Date) {
    const start = startDate.toISOString().slice(0, 10) // 'yyyy-MM-dd'
    const end = endDate.toISOString().slice(0, 10)

    return this.httpRepository.getList<OrderProductResponse>(
      {
        path: `/api/orders?page=${page}&startDate=${encodeURIComponent(start)}&endDate=${encodeURIComponent(end)}`,
      },
      OrderProductResponse,
    )
  }

  public cancel() {
    return this.httpRepository.getAll<OrderProductRequest>(
      {
        path: `/api/orders/cancel`,
      },
      OrderProductRequest,
    )
  }

  public getOrderCount() {
    return this.httpRepository.get<UserOrderCount>(
      {
        path: `/api/orders/count`,
      },
      UserOrderCount,
    )
  }


}

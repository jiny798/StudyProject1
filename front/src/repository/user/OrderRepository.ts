import { inject, singleton } from 'tsyringe'
import HttpRepository from '@/repository/user/HttpRepository.ts'
import ProductInCart from '@/entity/order/user/ProductInCart.ts'
import type OrderRequest from '@/entity/order/user/OrderRequest.ts'
import ResponseOrderProduct from '@/entity/order/user/OrderProductRequest.ts'
import OrderProduct from '@/entity/order/user/OrderProductRequest.ts'

@singleton()
export default class OrderRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}

  public order(request: OrderRequest) {
    return this.httpRepository.post({
      path: '/api/orders',
      body: request,
    })
  }

  public getOrders(startDate: Date, endDate: Date) {
    const start = startDate.toISOString().slice(0, 10) // 'yyyy-MM-dd'
    const end = endDate.toISOString().slice(0, 10)

    return this.httpRepository.getAll<ResponseOrderProduct>(
      {
        path: `/api/order?startDate=${encodeURIComponent(start)}&endDate=${encodeURIComponent(end)}`,
      },
      ResponseOrderProduct,
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

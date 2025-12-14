import{ inject, singleton } from 'tsyringe'
import HttpRepository from '@/repository/user/HttpRepository.ts'
import type RequestProduct from '@/entity/order/user/RequestProduct.ts'
import ProductInCart from '@/entity/order/user/ProductInCart.ts'
import type OrderRequest from '@/entity/order/user/OrderRequest.ts'
import ResponseOrderProduct from '@/entity/order/user/OrderProductRequest.ts'
import OrderProduct from '@/entity/order/user/OrderProductRequest.ts'
import AdminOrderSummary from "@/entity/order/admin/AdminOrderSummary.ts";

@singleton()
export default class AdminOrderRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}

  public order(request: OrderRequest) {
    return this.httpRepository.post({
      path: '/api/order',
      body: request,
    })
  }

  public getOrders(page: number) {
    return this.httpRepository.getAll<AdminOrderSummary>(
      {
        path: `/api/admin/orders?page=${page}&size=10`,
      },
      AdminOrderSummary,
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

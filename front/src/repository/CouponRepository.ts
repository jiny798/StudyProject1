import {inject, singleton} from 'tsyringe'
import HttpRepository from '@/repository/HttpRepository'
import AddCart from '@/entity/cart/AddCart.ts'
import Product from "@/entity/product/Product.ts";
import CartResponse from "@/entity/cart/CartResponse.ts";

@singleton()
export default class CouponRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {
  }

  public write(request: AddCart) {
    return this.httpRepository.post({
      path: '/api/cart',
      body: request,
    })
  }

  public get() {
    return this.httpRepository.get<CartResponse>({path: `/api/cart`}, CartResponse)
  }

}

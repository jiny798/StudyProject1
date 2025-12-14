export default class CartResponse {
  public cartItems: CartItem[] = [];
  public totalPrice: number = 0;
}

export class CartItem {
  public cartItemId: number = 0;
  public productId: number = 0;
  public productName: string = "";
  public quantity: number = 0;
  public price: number = 0;
  public optionId: number = 0;
  public optionDescription: string = "";
}

export default class PostWrite {
  public name = ''
  public price: number | null = null
  public categoryId: number | null = null
  public description = ''
  public productImages: string[] = []
  public options: Record<string, any> = {}
}

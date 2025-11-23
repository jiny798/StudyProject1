import { Type } from 'class-transformer'

export default class Category {
  public id = 0
  public name = ''
  public parentId: number | null = null

  @Type(() => Category)
  public children: Category[] = []
}

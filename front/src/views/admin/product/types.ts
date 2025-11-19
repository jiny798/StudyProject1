export interface Option {
  id: number
  optionName: string
  optionValues: OptionValue[]
}

export interface OptionValue {
  id: number
  valueName: string
  stock: number | null
  subOptions: Option[]
}
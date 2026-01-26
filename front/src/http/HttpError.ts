import type { AxiosError } from 'axios'

interface ErrorResponse {
  error?: {
    errorCode?: string;
    errorMessage?: string;
  }
}

export default class HttpError {
  private readonly errorCode: string
  private readonly errorMessage: string

  constructor(e: AxiosError<ErrorResponse>) {
    // const errorData = e.response?.data?.error as { errorCode?: string; errorMessage?: string }
    const errorData = e.response?.data?.error
    this.errorCode = errorData?.errorCode ?? '500'
    this.errorMessage = errorData?.errorMessage ?? '관리자에게 문의하세요'
  }

  public getMessage() {
    return this.errorMessage
  }
}

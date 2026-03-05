import { createToaster } from "@chakra-ui/react"

export const toaster = createToaster({
  placement: "top-end",
  pauseOnPageIdle: true,
  offsets: {
    top: "20px",
    right: "20px",
    bottom: "20px",
    left: "20px",
  },
})

type AlertType = "success" | "error" | "warning" | "info"

export function useAppAlert() {

  function showAlert(
    title: string,
    description?: string,
    type: AlertType = "info"
  ) {
    toaster.create({
      title,
      description,
      type,
      duration: 4000,
      closable: true,
    })
  }

  return {
    success: (title: string, description?: string) =>
      showAlert(title, description, "success"),

    error: (title: string, description?: string) =>
      showAlert(title, description, "error"),

    warning: (title: string, description?: string) =>
      showAlert(title, description, "warning"),

    info: (title: string, description?: string) =>
      showAlert(title, description, "info"),
  }
}
import { useState } from "react"

interface UseStepperProps {
  totalSteps: number
  initialStep?: number
}

export function useStepper({ totalSteps, initialStep = 0 }: UseStepperProps) {
  const [step, setStep] = useState(initialStep)

  const next = () => {
    setStep((prev) => (prev < totalSteps - 1 ? prev + 1 : prev))
  }

  const prev = () => {
    setStep((prev) => (prev > 0 ? prev - 1 : prev))
  }

  const goTo = (index: number) => {
    if (index >= 0 && index < totalSteps) {
      setStep(index)
    }
  }

  const reset = () => setStep(initialStep)

  const isFirstStep = step === 0
  const isLastStep = step === totalSteps - 1

  return {
    step,
    next,
    prev,
    goTo,
    reset,
    isFirstStep,
    isLastStep,
  }
}
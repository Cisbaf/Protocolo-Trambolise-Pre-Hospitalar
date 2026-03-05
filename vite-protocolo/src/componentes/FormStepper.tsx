import { useStepper } from "../hooks/useStepper"
import { LinhaDoTempoSection } from "./LinhaDoTempoSection"
import { Stepper } from "./Stepper"

export function FormStepper() {


  const steps = [
    {
      title: "LINHA DO TEMPO E IDENTIFICAÇÃO",
      description: "",
      content: <LinhaDoTempoSection />,
    },
  ]

  const stepper = useStepper({ totalSteps: steps.length })

  return (
    <Stepper
      steps={steps}
      step={stepper.step}
      next={stepper.next}
      prev={stepper.prev}
      isFirstStep={stepper.isFirstStep}
      isLastStep={stepper.isLastStep}
    />
  )
}
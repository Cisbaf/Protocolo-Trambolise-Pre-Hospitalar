import { Button, ButtonGroup, Steps } from "@chakra-ui/react"


interface StepItem {
  title: string
  description?: string
  content: any;
}

interface StepperProps {
  steps: StepItem[]
  step: number
  next: () => void
  prev: () => void
  isLastStep: boolean
  isFirstStep: boolean
}

export function Stepper({
  steps,
  step,
  next,
  prev,
  isLastStep,
  isFirstStep,
}: StepperProps) {
  return (
    <Steps.Root step={step} count={steps.length}>
      <Steps.List>
        {steps.map((item, index) => (
          <Steps.Item key={index} index={index} title={item.title}>
            <Steps.Indicator />
            <Steps.Title>{item.title}</Steps.Title>
            <Steps.Separator />
          </Steps.Item>
        ))}
      </Steps.List>

      {steps.map((item, index) => (
        <Steps.Content key={index} index={index}>
          {item.description && <p>{item.description}</p>}
          {item.content}
        </Steps.Content>
      ))}

      <Steps.CompletedContent>
        Todos os passos foram concluídos!
      </Steps.CompletedContent>

      <ButtonGroup size="sm" variant="outline" mt={4}>
        <Button onClick={prev} disabled={isFirstStep}>
          Voltar
        </Button>

        <Button onClick={next} disabled={isLastStep}>
          Próximo
        </Button>
      </ButtonGroup>
    </Steps.Root>
  )
}
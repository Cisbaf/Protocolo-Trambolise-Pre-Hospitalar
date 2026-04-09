import React from "react";
import { useAvcForm } from "../hooks/useAvcForm";
import { LoadingOverlay } from "../componentes/ui/loading";
import { usePost } from "../hooks/usePost";
import { BaseURL } from "../settings";
import { fakeAvcFormInitialValues } from "../forms/formRegisterAvc/utils/fakeInitialValues";
import { ErrorToaster, FormErrorToaster, SuccessToaster } from "../forms/formRegisterAvc/utils/toasters";

interface AvcFormType {
  form: ReturnType<typeof useAvcForm>
}

interface AvcFormProps {
  children: any;
  fakeInitialValues?: boolean;
}

const AvcFormContext = React.createContext<AvcFormType | null>(null);

export function AvcFormProvider(props: AvcFormProps) {
    const avcForm = useAvcForm(props.fakeInitialValues? fakeAvcFormInitialValues : undefined);

    const { post, loading } = usePost({
      url: `${BaseURL}/protocolo`,
      onSuccess(data) {
        SuccessToaster(data.id);
        avcForm.reset();
      },
      onError: (error)=>ErrorToaster(error.message),
    })

    return (
       <AvcFormContext.Provider value={{form: avcForm}}>
         <form onSubmit={avcForm.handleSubmit(post, FormErrorToaster)}>
            <LoadingOverlay isOpen={loading} />
            {props.children}
        </form>
       </AvcFormContext.Provider>
    )
}

export function useAvcFormContext() {
  const context = React.useContext(AvcFormContext)

  if (!context) {
    throw new Error(
      "useAvcForm must be used inside AvcFormProvider"
    )
  }

  return context
}
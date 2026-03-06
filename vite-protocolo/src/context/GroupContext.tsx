import React from "react";
import { useDataForm } from "../hooks/useDataForm";
import { fakeInitialValues } from "../utils/formFakeValueInit";

interface GroupContextType {
  form: ReturnType<typeof useDataForm>

}

const GroupContext = React.createContext<GroupContextType | null>(null);

interface GroupContextProviderProps {
    children: any;
}

export function GroupContextProvider({children}: GroupContextProviderProps) {

  const methodsForm = useDataForm();

  return <GroupContext.Provider
      value={{
          form: methodsForm
      }}>{children}</GroupContext.Provider>
}

export function useGroupContext() {
  const context = React.useContext(GroupContext)

  if (!context) {
    throw new Error(
      "useGroupContext must be used inside GroupContextProvider"
    )
  }

  return context
}
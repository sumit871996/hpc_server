## Endpoints

|    Sr no    | Use case  | endpoint |
| --------  | ---------| --------|
| 1        | To fetch types available | http://localhost:8081/form/getusecases | 
| 2 | To fetch JSON RJSF Schema for particular usecase from useCase Id fetched by previous endpoint | http://localhost:8081/form/getusecases/{useCaseId} |
| 3 | To get dockerfile for usecase ID | http://localhost:8081/form/getdockerfile/{useCaseId}


## RJSF Help
1. https://rjsf-team.github.io/react-jsonschema-form/
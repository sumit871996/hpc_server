## Endpoints

|    Sr no    | Use case  |  method  |  endpoint |
| --------  | ---------| --------| ----- |
| 1        | To fetch types available | get | http://localhost:8081/form/getusecases | 
| 2 | To fetch JSON RJSF Schema for particular usecase from useCase Id fetched by previous endpoint | get | http://localhost:8081/form/getusecases/{useCaseId} |
| 3 | To get dockerfile for usecase ID | post | http://localhost:8081/form/getdockerfile/{useCaseId}

## See postman collection for more details

## RJSF Help
1. https://rjsf-team.github.io/react-jsonschema-form/
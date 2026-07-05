# Anotacao para o frontend: exclusao de perfil

## Paciente

- O paciente so pode fazer exclusao logica do proprio perfil.
- Use `DELETE /auth/me` quando o paciente clicar para excluir/desativar a propria conta.
- Resposta esperada: `204 No Content`.
- Esse endpoint tambem limpa os cookies de sessao/login, entao o frontend deve redirecionar para a tela publica ou login apos sucesso.
- Nao exibir acao de exclusao total para paciente.

## Admin

- O admin pode fazer exclusao logica ou exclusao total de um perfil.
- Exclusao logica: `PATCH /admin/users/{userId}/status`
- Corpo aceito:

```json
{
  "active": false
}
```

ou:

```json
{
  "status": "INACTIVE"
}
```

- Para reativar, envie `active: true` ou `status: "ACTIVE"`.
- Exclusao total: `DELETE /admin/users/{userId}`.
- Resposta esperada da exclusao total: `204 No Content`.
- Antes de chamar exclusao total, o frontend deve pedir confirmacao explicita, porque o registro do usuario e o perfil vinculado sao removidos definitivamente.

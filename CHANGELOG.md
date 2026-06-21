# Changelog

## v1.1
- 베이스 패키지 설정화: `config.md`의 `base_package` 한 줄로 전체 패키지 루트 제어 (`--pkg=` 인자 오버라이드 지원)
- 패키지 선언 + 레이어 간 `import`를 모든 템플릿에 포함 → 실제 프로젝트로 바로 포팅 가능
- `runCatching` 취소 예외 버그 수정: ViewModel이 `try/catch`로 `CancellationException`을 rethrow
- 테스트 스캐폴드 추가: 기능별 UseCase 테스트 + ViewModel 테스트 생성 (`templates/test.md`)
- 필드 커스터마이징: `FeatureName:field[:Type],...` 문법으로 도메인 모델·DTO·Mapper 필드 지정
- `presentation` 레이어를 `ui`로 표준화

## v1.0
- domain / data / ui 3레이어 클린 아키텍처 골격 생성
- Hilt DI 모듈(Api `@Provides` + Repository `@Binds`)과 공통 `NetworkModule` 연동
